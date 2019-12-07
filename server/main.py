from flask import Flask, request, url_for
import sqlalchemy as db
from flask_cors import CORS
import json
import requests

app = Flask(__name__)
CORS(app)

def connectDB():
    return db.create_engine(db.engine.url.URL(
        drivername="mysql+pymysql",
        username="admin",
        password="maxcantcode123",
        database="pasteyboi",
        query={"unix_socket": "/cloudsql/{}".format("pasteyboi:europe-west2:pasteyboi")},
    )).connect()


@app.route("/")
def index():
    with connectDB() as con:
        query = db.sql.text("SELECT * FROM users;")
        results = con.execute(query)

        return str(results.rowcount)


@app.route("/createUser", methods=["POST"])
def createUser():
    req = request.json

    with connectDB() as con:
        query = db.sql.text("INSERT INTO users (userID, userName, password) VALUES ('" + req["userID"] + "', '" + req["userName"] + "', '" + req["password"] + "');")
        con.execute(query)

    return str(json.dumps({
        "code": 200,
        "message": "User Created"
    }))


@app.route("/signinUser", methods=["GET"])
def signinUser():
    req = request.json
    ret = {}

    with connectDB() as con:
        query = db.sql.text("SELECT * FROM users WHERE userName = '" + req["userName"] + "' AND password='" + req["password"] +"';")
        res = con.execute(query)

        ret["status"] = 401 if res.rowcount != 1 else 200

    return json.dumps(ret)


@app.route("/dump", methods=["POST"])
def dump():
    req = request.json

    with connectDB() as con:
        query = db.sql.text("INSERT INTO dumps (userID, dumpID) VALUES ('" + req["userID"] + "', '" + req["dumpID"] + "')")
        res = con.execute(query)

        for file in req["contents"]:
            query = db.sql.text("INSERT INTO fileDumps (dumpID, fileIndex, fileName, contents) VALUES ('" + req["dumpID"] + "', '" + file["fileIndex"] + "', '" + file["fileName"] + "' ,'" + file["body"] + "')")
            con.execute(query)


@app.route("/getDump", methods=["GET"])
def getDump():
    req = request.json
    ret = {
        "dumpID": req["dumpID"],
        "contents": [],
    }

    with connectDB() as con:
        query = db.sql.text("SELECT (fileIndex, fileName, contents) FROM fileDumps WHERE dumpID = '" + req["dumpID"] + "' ORDER BY fileIndex;")
        res = con.execute(query).fetchall()

        for file in res:
            ret["contents"].append(file)

    return json.dumps(ret)


@app.route("/getUserDumps", methods=["GET"])
def getUserDumps():
    req = request.json
    ret = []

    with connectDB() as con:
        query = db.sql.text("SELECT dumpID FROM dumps WHERE userID='" + req["userID"] + "';")
        userDumps = con.execute(query).fetchall()

        for id in userDumps:
            ret.append(requests.post(url_for("getDump"), json={"dumpID": req["dumpID"]}))
