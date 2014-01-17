uniform mat4 proscene_projection;
uniform mat4 proscene_view;
uniform mat4 proscene_model;

attribute vec4 vertex;
attribute vec4 color;

varying vec4 vertColor;

void main() {
  gl_Position = proscene_projection * proscene_view * proscene_model * vertex;
  vertColor = color;
}
