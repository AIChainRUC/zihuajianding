# -*- coding: utf-8 -*-
import os
from flask import Flask, request, redirect, url_for

UPLOAD_FOLDER = './upload'

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/upload/', methods=['POST'])
def upload_file():
    if request.method == 'POST':
    	name = request.form.get('name', 'filename')	
        file = request.files['img']
        if file:
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], name))
            return 200
        else:
        	return 500
    else:
	return 500  

if __name__ == '__main__':
	app.run(host='0.0.0.0')
