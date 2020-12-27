from flask import Flask
from flask import request
from werkzeug.utils import secure_filename
import sqlite3
import os

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = '/var/www/html/server/uploads'

def add_user(conn, userId, email, phone, password):
    conn.execute("INSERT INTO Users VALUES (?,?,?,?)", [userId, email, phone, password])
    conn.commit()

def create_tables(conn):
        cursor = conn.cursor()
        cursor.execute(""" CREATE TABLE Users(id TEXT PRIMARY KEY,
                                                      email TEXT,
                                                      phone TEXT,
                                                      password TEXT)""")
        conn.commit()

def open_new_db(path):
    file = open(path, 'w+')
    file.close()

db_path = os.path.join(app.config['UPLOAD_FOLDER'], "users.db")
if not os.path.exists(db_path):
    open_new_db(db_path)
    conn = sqlite3.connect(db_path)
    create_tables(conn)
    conn.close()


@app.route("/")
def hello():
    return "hello world server!"

@app.route("/upload/image", methods=['POST'])
def saveImage():
    file = request.files['image']
    filename = secure_filename(file.filename)
    basedir = os.path.abspath(os.path.dirname(__file__))
    file.save(os.path.join(basedir, app.config['UPLOAD_FOLDER'], filename))
    return 'true'

@app.route("/add_user", methods=['GET'])
def saveUserInfo():
    userId = request.args.get('id')
    email = request.args.get('email')
    phone = request.args.get('phone')
    password = request.args.get('password')
    db_path = os.path.join(app.config['UPLOAD_FOLDER'], "users.db")
    conn = sqlite3.connect(db_path)
    add_user(conn, userId, email, phone, password)
    return 'true'

if __name__ == "__main__":
    app.run()

