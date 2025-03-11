import sys
import base64
from io import BytesIO
from PIL import Image
import numpy as np
from deepface import DeepFace
import cv2
import numpy as np
from deepface import DeepFace

def load_and_resize_image(image_path, target_size=(640, 480)):
    image = cv2.imread(image_path)
    if image is None:
        print(f"Failed to load image at path: {image_path}")
        return None

    # Resize image to the target size
    image_resized = cv2.resize(image, target_size)
    return image_resized

# Function to load an image and detect a face
def detect_face(image_path,number):
    # Load image
    image = load_and_resize_image(image_path, target_size=(640, 480))

    if image is None:
        return None

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Load the pre-trained face detector
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    # Detect faces
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(50, 50))

    if len(faces) == 0:
        print("No face detected"+number)
        return None

    # Assuming only one face is detected, return the first one
    x, y, w, h = faces[0]
    face_image = image[y:y + h, x:x + w]

    return face_image


# Function to compare two faces
def compare_faces(face1_path, face2_path):
    try:
        # Use DeepFace for comparing the two face images
        result = DeepFace.verify(face1_path, face2_path)
        print("Comparison Result:", result)
        return result
    except Exception as e:
        print(f"Error during face comparison: {e}")
        return None


# Main logic
if __name__ == "__main__":
    # Paths to the images you want to compare
    image1_path = sys.argv[1]
    image2_path = sys.argv[2]

    image1 = cv2.imread(image1_path)
    if image1 is None:
        print(f"Failed to load image at path: {image1_path}")

    image2 = cv2.imread(image2_path)
    if image2 is None:
        print(f"Failed to load image at path: {image2_path}")

    # Detect faces (optional step)
    face1_image = detect_face(image1_path,"1")
    face2_image = detect_face(image2_path,"2")


    if face1_image is not None and face2_image is not None:
        # Save cropped faces for DeepFace comparison
        cv2.imwrite("face1_cropped.png", face1_image)
        cv2.imwrite("face2_cropped.png", face2_image)

        # Compare the two faces using DeepFace
        comparison_result = compare_faces("face1_cropped.png", "face2_cropped.png")

        if comparison_result:
            if comparison_result['verified']:
                print("Faces match!")
            else:
                print("Faces do not match.")
        else:
            print("Comparison failed.")
