# First, convert Asciidoctor to DocBook

gem install asciidoctor   || echo "asciidoctor is already installed"
gem install asciidoctor-pdf --pre || echo "asciidoctor-pdf is already installed"
brew install pandoc || echo "pandoc is already installed"


asciidoctor -b docbook README.adoc -o publish/file.xml
# Then, convert DocBook to Markdown
cp -r images publish
pandoc -f docbook -t markdown publish/file.xml -o publish/README.md

#pandoc -f docbook -t markdown publish/file.xml -o publish/README.md
#markdown publish/README.md  > publish/README.html
