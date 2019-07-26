### Catalog source (the CSV File)

A CSV (Comma Separated Values) file is a type of data which can be compared to an Excel sheet, with rows of text containing values separated by a separator (the separator does not have to mandatory be a comma). It is a simple file format, but it has its own rules, for example values containing the separator should be enclosed in quotes

It is a very common format, normally any data dealing application should have a way to export its data into CSV, and from these what interest us are databases and Excel files.
In the case of databases, it is important to see the catalog CSV as the result of a query (SQL) which brings all the needed data, links (joins) all the tables that have the data, and filters and sorts them. It is important to note that this include also all the parametrical tables (the ones that only keep names/labels for keys) such as country and state names, department names etc. So, the resulting query will repeat the name (label) of a record (not the key).
In case of Excel, is important to have each column of the same data type (with exceptions) for the result to be a like a table of the data

Is important that the first line in the CSV file to be a header line, with the desired names of the columns. In SQL this could be done by using "AS" in "SELECT" and in Excel it should be the first line of the sheet. If it is not possible to have the header line (the CSV is provided by a third party and is too big to be edited) the application will still work, but it should be specified that there is no header line

After the CSV is created (obtained from somewhere) it can be viewed (displayed) in the application in order to see if it's OK and how the data looks like

[![View CSV]( ../screenshots/static-catalog--view-csv.png)]( ../screenshots/static-catalog--view-csv.png)

The application was designed to be able to handle large data, so the viewer will not display whole files, but only the first lines (default 1001 meaning 1 header line and 1000 data lines, and for data up to this count the columns will have their widths sized, and maximum 100001 meaning 1 header line and 100000 data lines). However, even if not entirely displayed, the whole file will be parsed, and the total number of lines will be shown, and if the parsing is not OK an error will be provided. This step gives a preview of the data and prepares for the next step, the examination of the data

During this preparation, please consider:
1. Decide what data is intended to be searched/displayed on the final HTML page. If a field will not be used it is recommended to remove it from the CSV, as it will only take up space
2. Try to decide what fields (columns) will be used to filter the data
3. Try to decide what fields (columns) will be used to sort the data

