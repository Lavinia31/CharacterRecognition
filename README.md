# CharacterRecognition
# Recognition of Graphic Symbols on Android Platforms Using Low-Complexity Convolutional Neural Networks

## Description
This project represents my undergraduate thesis and focuses on the recognition of graphic symbols using convolutional neural networks (CNN) implemented on Android platforms. The main objective is to develop and optimize low-complexity CNN models that can efficiently operate on mobile devices with limited resources.

## Objectives
- Identify and preprocess relevant datasets (EMNIST, SVHN, etc.)
- Implement and optimize at least three low-complexity CNN models (EfficientNet, V-CNN, MobileNet, etc.)
- Convert the trained models to TFLite format for use on Android platforms
- Develop an Android application that integrates the trained models and allows the recognition of graphic symbols
- Evaluate the application's performance in terms of accuracy and response time

## Datasets
- **EMNIST**: An extended dataset derived from MNIST, containing black and white images of handwritten characters and symbols. https://www.kaggle.com/datasets/laviniaioanavlad/emnist3
- **SVHN**: A dataset containing color images of digits extracted from house number plates.

## CNN Models Used
- **EfficientNet**
- **V-CNN**
- **MobileNet**
- **ShuffleNet**

## Technologies and Libraries Used
- **Python**: Keras, TensorFlow, Scikit-Learn
- **Java**: TFLite
- **Development Environments**: Kaggle, Android Studio

## Results and Conclusions
The developed and optimized models demonstrated variable performances depending on the dataset used. EfficientNet and MobileNet provided an accuracy of approximately 92% for the EMNIST dataset, while MobileNet was the most efficient for SVHN, with an accuracy of 94.63%. The models were converted to TFLite format and integrated into an Android application(code found in master branch), which was successfully tested in real scenarios.

## Future Directions
- Improving model accuracy through additional training and optimizations
- Extending the Android application to include the recognition of other types of symbols and characters
- Exploring other datasets and CNN architectures to improve performance


https://github.com/user-attachments/assets/54c7b49e-9539-401e-8975-40cb0c767fea



I would like to acknowledge the invaluable guidance and support provided by my supervisors, 
Conf. dr. ing. Ioana DOGARU & Prof. dr. ing. Radu DOGARU.

