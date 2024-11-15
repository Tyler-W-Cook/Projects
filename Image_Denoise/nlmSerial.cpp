#include <iostream>
#include <vector>
#include <cmath>
#include <algorithm>
#include <chrono>

using namespace std;

// Constants
const int SEARCH_RADIUS = 5;   // Half-size of the search window
const int FILTER_RADIUS = 2;   // Half-size of the patch
const float SIGMA = 10.0f;     // Filtering parameter for Gaussian similarity
const float EPSILON = 1e-5f;   // Small value to prevent division by zero

// Gaussian function for similarity calculation
inline float gaussian(float x, float sigma) {
    return expf(-x * x / (2.0f * sigma * sigma));
}

// Non-Local Means filter
void nonLocalMeans(const vector<vector<float>>& input, vector<vector<float>>& output, int width, int height) {
    // Iterate through each pixel in the image
    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            float filteredPixel = 0.0f;
            float normFactor = 0.0f;

            // Center pixel intensity
            float centerPixel = input[y][x];

            // Iterate through the search window
            for (int dy = -SEARCH_RADIUS; dy <= SEARCH_RADIUS; ++dy) {
                for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; ++dx) {
                    int nx = x + dx;
                    int ny = y + dy;

                    // Check if the neighbor is inside the image boundaries
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        float neighborPixel = input[ny][nx];

                        // Calculate weight using Gaussian similarity
                        float weight = gaussian(centerPixel - neighborPixel, SIGMA);

                        filteredPixel += weight * neighborPixel;
                        normFactor += weight;
                    }
                }
            }

            // Normalize the result and assign to output
            output[y][x] = filteredPixel / (normFactor + EPSILON);
        }
    }
}

int main() {
    // Example image (grayscale, normalized to [0, 1])
    int width = 8, height = 8;
    vector<vector<float>> image = {
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9},
        {0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.9, 0.9}
    };

    vector<vector<float>> output(height, vector<float>(width, 0.0f));

    // Measure execution time
    auto start = chrono::high_resolution_clock::now();
    nonLocalMeans(image, output, width, height);
    auto end = chrono::high_resolution_clock::now();

    // Print results
    cout << "Input Image:" << endl;
    for (const auto& row : image) {
        for (float pixel : row) {
            cout << pixel << " ";
        }
        cout << endl;
    }

    cout << "\nDenoised Image:" << endl;
    for (const auto& row : output) {
        for (float pixel : row) {
            cout << pixel << " ";
        }
        cout << endl;
    }

    // Print execution time
    chrono::duration<double> elapsed = end - start;
    cout << "\nExecution time: " << elapsed.count() << " seconds" << endl;

    return 0;
}
