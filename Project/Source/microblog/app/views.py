from flask import jsonify

@app.route('/'), methods=['POST'])
@app.route('/imageClassify', methods=['POST'])
def imageClassify():
    return jsonify({
        'text': imageClassify(
            request.form['text'],
            request.form['sourceLang'],
            request.form['destLang']) })