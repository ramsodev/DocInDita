# XMLDocInDita

The objective of this project is to generate documentation of various types of XML definitions (XSD, WSDL and WADL) in [DITA][a3775041].

## Usage
`java -jar XMLDocInDita-1.0.0-jar-with-dependencies.jar [path]`
### Options
usage: XMLDocInDiata [-description \<arg\>] [-h] [-id \<arg\>] [-one] [-outDir        \<arg\>] [-r] [-title \<arg\>]
- description \<arg\>: Short Description for the Cover
- h:                   Help
- id \<arg\>:            id of the document.
- one:                 Add all de documentation in only one map
- outDir \<arg\>:        Path to the output directory
- r:                   If the path is a dir read recursibley al the child dirs
- title \<arg\>:         Title for the Cover

## documentation
- [Java Api][c036cfd1]

  [a3775041]: https://www.dita-ot.org/ "dita-ot"
  [c036cfd1]: apidocs/index.html "JavaDoc"
