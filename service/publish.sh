# First, convert Asciidoctor to DocBook

gem install asciidoctor   || echo "asciidoctor is already installed"
gem install asciidoctor-pdf --pre || echo "asciidoctor-pdf is already installed"
brew install pandoc || echo "pandoc is already installed"


cp -r images publish

## markdown first
gem exec asciidoctor -b docbook README.adoc -o publish/file.xml
pandoc -f docbook -t markdown publish/file.xml -o publish/README.md

