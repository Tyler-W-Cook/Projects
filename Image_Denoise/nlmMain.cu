
// Host call
void nlmDenoise(const float* h_image_in, float* h_image_out, int width, int height) {

    float* d_image_in;  //image arrays
    float* d_image_out;
    size_t imageSize = width * height * sizeof(float);

    // Allocate gpu mem for calc
    cudaMalloc(&d_image_in, imageSize);
    cudaMalloc(&d_image_out, imageSize);

    // Copy image to device
    cudaMemcpy(d_image_in, h_image_in, imageSize, cudaMemcpyHostToDevice);

    // Set up timing events for perfomance bench
    cudaEvent_t start, stop;

    cudaEventCreate(&start);
    cudaEventCreate(&stop);

    // Define grid and block size
    dim3 blockSize(BLOCK_SIZE, BLOCK_SIZE);
    dim3 gridSize((width + BLOCK_SIZE - 1) / BLOCK_SIZE, (height + BLOCK_SIZE - 1) / BLOCK_SIZE);

    // Start timing
    cudaEventRecord(start);

    // Make kernel call
    nlmDenoise<<<gridSize, blockSize>>>(d_image_in, d_image_out, width, height);

    // Stop timing
    float ms = 0;
    cudaEventRecord(stop);
    cudaEventSynchronize(stop);

    cudaEventElapsedTime(&ms, start, stop);
    printf("Execution time: %f ms\n", ms);

    // Copy image back to computer
    cudaMemcpy(h_image_out, d_image_out, imageSize, cudaMemcpyDeviceToHost);

    // Free mem
    cudaFree(d_image_in);
    cudaFree(d_image_out);

    cudaEventDestroy(start);
    cudaEventDestroy(stop);
}

