import os
from imutils.video import FileVideoStream
from imutils.video import VideoStream
from imutils import face_utils
from ear import calculateEAR
from constants import *
import numpy as np
import imutils
import cv2
import dlib
import time


def rotate(image, angle, center=None, scale=0.7):
	(h, w) = image.shape[:2]

	if center is None:
		center = (w / 2, h / 2)
	M = cv2.getRotationMatrix2D(center, angle, scale)
	rotated = cv2.warpAffine(image, M, (w, h))
	return rotated

def detect_blinks(videoFilePath):
	frameCount = 0
	blinkCount = 0

	faceDetector = dlib.get_frontal_face_detector()
	landmarksPredictor = dlib.shape_predictor(shapePredictorPath)

	leftEyeIdx = face_utils.FACIAL_LANDMARKS_IDXS['left_eye']
	rightEyeIdx = face_utils.FACIAL_LANDMARKS_IDXS['right_eye']

	if FILE_VIDEO_STREAM:
		vs = FileVideoStream(videoFilePath).start()
	else:
		vs = VideoStream().start()
	time.sleep(0.5)

	target_face_save_path = os.path.join(CURRENT_FOLDER_PATH, str(time.time()) + '.jpg')

	try:
		while True:
			if FILE_VIDEO_STREAM and not vs.more():
				break
			frame = vs.read()
			frame = rotate(frame, 90)
			frame = imutils.resize(frame, width = 500)
			gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
			target_face = gray
			faces = faceDetector(gray, 0)

			for (i, face) in enumerate(faces):
				(x, y, w, h) = face_utils.rect_to_bb(face)
	
				landmarks = landmarksPredictor(gray, face)
				landmarks = face_utils.shape_to_np(landmarks)
			
				leftEye = landmarks[leftEyeIdx[0]:leftEyeIdx[1]]
				rightEye = landmarks[rightEyeIdx[0]:rightEyeIdx[1]]
			
				leftEAR = calculateEAR(leftEye)
				rightEAR = calculateEAR(rightEye)
				ear = (leftEAR + rightEAR)/2.0
				if ear < EAR_THRESHOLD:
					frameCount += 1
				else:
					target_face = gray
					if frameCount >= EAR_CONSEC_FRAMES:
						blinkCount += 1 
					frameCount = 0
		cv2.imwrite(target_face_save_path, target_face)
	except:
		blinkCount = 0
	vs.stop()

	return blinkCount, target_face_save_path

if __name__ == '__main__':
	videoFilePath = 'upload/video_1524802319390.mp4'
	print detect_blinks(videoFilePath)
