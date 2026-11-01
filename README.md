# Block Averaging

This small Java program takes a JPG image and turns it into a pixelated version. It splits the image into squares, replaces each square with its average color, and shows the result building up on screen. The final image is saved as `result.jpg`.

It can run on a single thread or on several threads (one per CPU core) to speed things up. If the image is larger than your screen, it is only scaled down for display. The averaging itself is always done on the original image.

## Running

You need Java 11 or newer.

```
java Pixelate.java <file.jpg> <square_size> <S|M>
```

- `file.jpg`: the image to process
- `square_size`: the side of each square in pixels
- `S` runs on a single thread, `M` runs on multiple threads

Example:

```
java Pixelate.java photo.jpg 20 M
```

When it finishes, `result.jpg` is saved in the current folder. Close the window to exit.
