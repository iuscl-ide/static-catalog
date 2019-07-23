### Introduction into catalog generation

A lot of the data we encounter every day is static, meaning that it will not change anymore. This data is usually kept in a database in order to be searched by a client with different filters, or in other cases just offered as download of an XLS or CSV, in order to be opened in Excel and searched there.
However, this data is read only, it cannot be modified by the client or the server because by the nature of the data this would make no sense

The idea here is to leave the data on server, but to put it in such a format that would be possible to be searched from the client (from browser) without server intervention other than transmitting the data to the client. In fact the data will follow the format of a database, with index files, keyword files, sort files and data block files, but instead of these files to be used on server, they will be transferred as need and used on the browser, this way the server will see them as any other static data (images, styles, pages), and the browser will do the actual search processing.

**static-catalog** is composed of two main components:

1. A desktop application that takes the data and generates the server files
2. A JS engine that is part of the generation operation result and will do the actual search in the browser

In order to generate a search-able catalog, the data should be provided as a CSV file. The creation of the CSV file is external to **static-catalog** but the way it is created impacts a lot on the quality of the result.
The application will generate the catalog as a set of files. There are two distinct categories:
1. The catalog files containing the data on which the search is done, these are independent of the web page design
2. The page files, these are specific to the web page, and will use a special data file in order to know the search data. These files should be provided to the application, as they are specific to the desired web site. The application can help however if the page is given as a **Liquid** template and inside it follows the page catalog data rules, the application will take it and create the web (html) page.

Finally, the resulted catalog files should be copied on the server, and the site verified if it works as intended, and if not, it can be regenerated with different filters, sort options or page settings.

