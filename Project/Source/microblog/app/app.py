# We need to import the jsonify object, it will let us
# output json, and it will take care of the right string
# data conversion, the headers for the response, etc
import predict
from flask import json

from flask import Flask, jsonify, request

import os


# Initialize the Flask application
app = Flask(__name__)


# This route will return a list in JSON format
@app.route('/', methods=['GET', 'POST'])
def index():
    # This is a dummy list, 2 nested arrays containing some
    # params and values
    content = request.get_json(silent=True)
    print (content)
    n = json.dumps(content)
    j = json.loads(n)
    print (j['question'])
    question = j['question']
    answer = "placeholder"
    answer = predict.main(question)
    #os.system('predict.py')

    list = [
        {'param': 'answer', 'val': answer},
    ]
    # jsonify will do for us all the work, returning the
    # previous data structure in JSON
    return jsonify(results=list)

if __name__ == '__main__':
    app.run(
        #host="192.168.56.1",
        host="192.168.86.129",
        port=int("8080")
    )
