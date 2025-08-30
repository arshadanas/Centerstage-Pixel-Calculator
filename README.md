# Centerstage Backdrop Vision and Pixel Placement Algorithm
Personal project for FIRST® Tech Challenge 2023-24 CENTERSTAGE™ season game

Two main parts:
- OpenCV Vision
  - Scans and saves the locations of pixels already placed within the backdrop (scoring area this season)
- Optimal placement calculation
  - Determine the ideal color and location to place the next pixel on the backdrop to maximize immediate and future points
  - Note: calculations account for remaining number of pixels and their colors, but do **not** account for remaining match time.
