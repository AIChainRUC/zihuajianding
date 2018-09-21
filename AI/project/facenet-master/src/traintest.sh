python classifier.py TRAIN ../../MDD_train/ ../../log/20180110-155805/ ../../classifier.pkl --batch_size=32 --image_size=224
python classifier.py CLASSIFY ../../MDD_train/ ../../log/20180110-155805/ ../../classifier.pkl --batch_size=32 --image_size=224
