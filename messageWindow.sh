MESSAGE=$1
TITLE=$2
echo $TITLE
echo $MESSAGE
groovy -e 'def f = new groovy.swing.SwingBuilder().frame(title:"'"${TITLE}"'",defaultCloseOperation:javax.swing.WindowConstants.EXIT_ON_CLOSE) { label(text:"'"${MESSAGE}"'",font: new java.awt.Font("Dialog",java.awt.Font.PLAIN,144)) };f.locationRelativeTo = null;f.pack();f.show()'
