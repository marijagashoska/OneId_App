# OneID 

OneID is a smart identity verification system that combines Optical Character Recognition (OCR) and Facial Recognition using machine learning to validate user identities with high accuracy and security. The system automates the process of checking if the information extracted from a user's ID card matches their live face scan, offering a reliable and secure method for digital identity verification.

## Key Features:
1. OCR-Based ID Validation:
The system reads the name, surname, and EMBG (unique ID number) from an uploaded photo of an ID card using EasyOCR, a machine learning-based text recognition library.

2. Face Detection on ID:
A face is detected and extracted from the ID image using OpenCV's Haar cascade classifier.

3. Live Face Matching:
The user provides a real-time selfie image. Both the ID face and the selfie are cropped, then compared using DeepFace, a facial recognition framework built on deep learning models.

4. Verification Result:
The system verifies whether the person on the ID matches the person in the selfie and returns a confirmation message accordingly.

## Technologies & Tools:
1. Python
2. OpenCV – for image processing and face detection
3. EasyOCR – for text extraction from ID cards
4. DeepFace – for facial comparison using deep neural networks
5. NumPy, Base64, JSON – for data handling and encoding

## Machine Learning:
1. OCR with Deep Learning:
Extracts relevant personal information from ID documents, reducing manual errors.
2. Facial Recognition:
Uses pre-trained models (e.g., VGG-Face, ArcFace) to compare facial features and verify identity with high accuracy.

  ![images/img1](https://github.com/marijagashoska/OneId_App/blob/master/images/img1.png)
  ![images/img2](https://github.com/marijagashoska/OneId_App/blob/master/images/img2.png)
  ![images/img3](https://github.com/marijagashoska/OneId_App/blob/master/images/img3.png)
  ![images/img4](https://github.com/marijagashoska/OneId_App/blob/master/images/img4.png)
