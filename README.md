# GCNW
We propose GCNW, which is a multimodal word representation model that is obtained by using a graph convolution network. GCNW is a word embedding model that realizes higher comprehensive performance because it incorporates additional multimodal information and uses effective mechanisms to process that information. In addition, the differences in the modality-relation matrix among tasks show that the model has satisfactory applicability. The incorporation of syntactic information can effectively improve the performance of the model. Similar to perceptual information, syntactic information is also needed for the multimodal word representation model. The method that is proposed in this paper can significantly improve the performance of word representation. Various experimental results support this conclusion.
To get the project started, you first need to construct two matrices: the modal relation matrix and the modal eigenmatrix
## How to construct the modal relation matrix?
You first need to prepare three modal representations, text mode, speech mode, and syntactic mode
For modal representation of text, any word containing semantics can be used to represent the model. In this paper, the default expression is Glove
Phonetic -and- Semantic embedding is recommended for speech modal representation,
The syntactic modal representation uses the probability distribution vector of the part of speech of the word, which can be obtained directly by calling the getPos method
Please be consistent in the dimensions of the above three representations
Finally, the manual integration is performed on line 346 of Learn.java
## How to construct the modal relation matrix?
The matrix is updated based on a greedy strategy and generally does not require manual construction
If you want to default parameters, you can override the initMatR method in Learn.java

Prepare the data set and modify the configuration path
Finally, just run Learn.java
