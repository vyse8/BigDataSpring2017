#from flask import Flask
#app = Flask(__name__)
#from app import views

from flask import Flask
from flask import jsonify
from flask import request
from app import predict

app = Flask(__name__)

if not app.debug:
    import logging
    from logging.handlers import RotatingFileHandler
    file_handler = RotatingFileHandler('app.log', 'a', 1 * 1024 * 1024, 10)
    file_handler.setFormatter(logging.Formatter('%(asctime)s %(levelname)s: %(message)s [in %(filename)s:%(lineno)d]'))
    app.logger.setLevel(logging.DEBUG)
    file_handler.setLevel(logging.DEBUG)
    app.logger.addHandler(file_handler)
    app.logger.info('App startup')

@app.route('/', methods=['GET', 'POST'])
def get_json():
    app.logger.info('Start of get_json')

    app.logger.info('Form data')
    app.logger.info(request.form)

    app.logger.info('Json data')
    app.logger.info(request.get_json())

    return 'OK'