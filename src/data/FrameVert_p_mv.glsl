uniform mat4 proscene_projection;
uniform mat4 proscene_modelview;

attribute vec4 vertex;
attribute vec4 color;

varying vec4 vertColor;

void main() {
  gl_Position = proscene_projection * proscene_modelview * vertex;
  vertColor = color;
}
