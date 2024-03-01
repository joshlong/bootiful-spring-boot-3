# First, convert Asciidoctor to DocBook

OUTPUT=output
gem install asciidoctor   || echo "asciidoctor is already installed"
gem install asciidoctor-pdf || echo "asciidoctor-pdf is already installed"
brew install pandoc || echo "pandoc is already installed"

mkdir -p $OUTPUT

cp -r images $OUTPUT

### markdown first
gem exec asciidoctor -b docbook README.adoc -o $OUTPUT/file.xml
pandoc -f docbook -t markdown $OUTPUT/file.xml -o $OUTPUT/README.md

RUBY_PATH=`dirname  $( which ruby )`/..
RUBY_PATH="$( cd  $RUBY_PATH && pwd )"
ED="$( gem env | grep "EXECUTABLE DIRECTORY:" | cut -f 2 -d : )"
APDF=`find $ED -iname "*asciidoctor-pdf"`
echo $APDF

$APDF README.adoc -o $OUTPUT/README.pdf
