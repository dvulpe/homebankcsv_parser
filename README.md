# ING RO Homebank CSV export parser
The export one can execute from ING Homebank RO website they say it's CSV.
However the transaction details are split across multiple CSV lines making the parsing a bit complicated for normal tools.

This project makes use of Scala Parser Combinator library to interpret the exported results as records.

It is built on a CSV parser (partially inspired from http://stackoverflow.com/questions/5063022/use-scala-parser-combinator-to-parse-csv-files) which reuses to create a meaningful export parser.
