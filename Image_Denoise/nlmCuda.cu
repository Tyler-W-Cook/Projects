#include <stdio.h>

#define BLOCK_SIZE 16  

#define FILTER_RADIUS 2 
#define SEARCH_RADIUS 5 
#define STANDARD_DEV 10.0    

// CUDA kernel for non-local means denoising
__global__ void nlmDenoise(const float* input, float* output, int width, int height) {

    //Indexing of threads
    int x = blockIj.x * blockDim.x + threadIj.x;
    int y = blockIj.y * blockDim.y + threadIj.y;

    //boundary check
    if (x >= width || y >= height) return;

    float filteredSum = 0.0;                //filtered value Sum
    float normSum = 0.0;                    //normalized Sum 

    float noisy_value = input[y * width + x];       //pixel value to be denoised

    for (int i = 0-x; i <= height; i++) {             // i is searhcing in the y
        for (int j = 0-y; j <= width; j++) {         // j is searching in the x
            int nx = x + j;
            int ny = y + i;

            // Boundary check to ensure the values are valid
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                float otherPixel = input[ny * width + nx];
                float weight = expf(-(noisy_value - otherPixel) * (noisy_value - otherPixel) / (STANDARD_DEV * STANDARD_DEV));
                filteredSum += weight * otherPixel;
                normSum += weight;
            }
        }
    }

    output[y * width + x] = filteredSum / normSum;
}
