import sys
import json
import cv2
import easyocr
import numpy as np
import base64

def resize_image(image, max_size=(1700, 1000)):
    """Resize image while maintaining aspect ratio."""
    height, width = image.shape[:2]

    if width > max_size[0]:
        scale_factor = max_size[0] / width
        new_width = max_size[0]
        new_height = int(height * scale_factor)
        image = cv2.resize(image, (new_width, new_height), interpolation=cv2.INTER_AREA)

    if height > max_size[1]:
        scale_factor = max_size[1] / height
        new_height = max_size[1]
        new_width = int(width * scale_factor)
        image = cv2.resize(image, (new_width, new_height), interpolation=cv2.INTER_AREA)
    return image

def extract_text_and_face(input_json):
    # Parse the input JSON
    data = json.loads(input_json)

    name = data['name']
    surname = data['surname']
    embg = data['embg']

    # Extract image and decode from base64
    image_bytes = base64.b64decode(data['image'])
    nparr = np.frombuffer(image_bytes, np.uint8)
    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    if image is None:
        return json.dumps({"error": "Invalid image data"})

    image = resize_image(image)
    # Extract detected text
    text_results = "\n".join(text for _, text, _ in easyocr.Reader(['en']).readtext(image))
#     found_name = name in text_results
#     found_surname = surname in text_results
#     found_embg = embg in text_results
    lines = text_results.splitlines()

    # Check if the full name, surname, and EMGB are in any of the lines
    found_name = any(name == line for line in lines)
    found_surname = any(surname == line for line in lines)
    found_embg = any(embg == line for line in lines)

        # If any of them are not found, add to result
    if not found_name:
        return json.dumps({
                "name": "Input incorrect for name-field"
            })
    if not found_surname:
        return json.dumps({
                "surname": "Input incorrect for surname-field"
            })
    if not found_embg:
        return json.dumps({
                "embg": "Input incorrect for embg-field"
            })

    # Load face detector (Haar cascade)
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")

    # Convert to grayscale for face detection
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Detect faces in image
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

    face_base64 = None  # Default value if no face found
    if len(faces) > 0:
        # Extract first detected face
        x, y, w, h = faces[0]
        face_region = image[y:y + h, x:x + w]

        # Encode face as Base64 string
        _, buffer = cv2.imencode(".png", face_region)
        face_base64 = base64.b64encode(buffer).decode('utf-8')

    # Return results as JSON
    return json.dumps({
        "face": face_base64
    })

if __name__ == "__main__":
    # Read input JSON from stdin (sent from Java)
    input_json = sys.stdin.read()

    # Process the input and get results
    result = extract_text_and_face(input_json)

    # Print the result to stdout (Java will read this)
    print(result)
