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


@app.route("/signinUser", methods=["POST"])
def signinUser():
    req = request.json

    with connectDB() as con:
        query = db.sql.text("SELECT * FROM users WHERE userName = '" + req["userName"] + "' AND password='" + req["password"] +"';")
        res = con.execute(query)

        return "401" if res.rowcount != 1 else "200"


@app.route("/dump", methods=["POST"])
def dump():
    req = request.json

    with connectDB() as con:
        query = db.sql.text("DELETE FROM dumps WHERE dumpID='" + req["dumpID"] + "' AND userID='" + req["userID"] + "';")
        res = con.execute(query)

        query = db.sql.text("DELETE FROM fileDumps WHERE dumpID='" + req["dumpID"] + "';")
        res = con.execute(query)

        con.close()

    with connectDB() as con:
        query = db.sql.text("INSERT INTO dumps (userID, dumpID) VALUES ('" + req["userID"] + "', '" + req["dumpID"] + "')")
        res = con.execute(query)

        for file in req["contents"]:
            query = db.sql.text("INSERT INTO fileDumps (dumpID, fileIndex, fileName, body) VALUES ('" + req["dumpID"] + "', '" + str(file["fileIndex"]) + "', '" + str(file["fileName"]) + "' ,'" + str(file["body"]) + "')")
            con.execute(query)

    return json.dumps({"status": 200})


@app.route("/getDump", methods=["GET"])
def getDump():
    req = request.args
    ret = {
        "dumpID": req["dumpID"],
        "contents": [],
    }

    with connectDB() as con:
        query = db.sql.text("SELECT * FROM fileDumps WHERE dumpID = '" + req["dumpID"] + "' ORDER BY fileIndex;")
        res = con.execute(query)

        for file in res:
            ret["contents"].append({
                "fileIndex": file[1],
                "fileName": file[2],
                "body": file[3]
            })

    return json.dumps(ret)


@app.route("/getUserDumps", methods=["GET"])
def getUserDumps():
    req = request.args
    ret = []

    with connectDB() as con:
        query = db.sql.text("SELECT * FROM dumps WHERE userID='" + req["userID"] + "';")
        userDumps = con.execute(query)

        for id in userDumps:
            query = db.sql.text("SELECT * FROM fileDumps WHERE dumpID = '" + id[0] + "' ORDER BY fileIndex;")
            res = con.execute(query)

            dmp = {
                "dumpID": id[0],
                "userID": req["userID"],
                "contents": []
            }

            for file in res:
                dmp["contents"].append({
                    "fileIndex": file[1],
                    "fileName": file[2],
                    "body": file[3]
                })

            ret.append(dmp)

    return json.dumps(ret)