###

**static-catalog** is a static site generator application which creates a search web page and a set of server files. The web page is then used to search and display the data contained in the files on the server, with all the search execution done in the browser.
This way, the server is used as a normal static HTTP server, with no server side or external script execution

Here are the steps of how it works:

* The site generation part is done in the application
	* First, a [CSV](https://en.wikipedia.org/wiki/Comma-separated_values) file must be obtained/created and it should contain all the data. This file will be the catalog. It could come from any source, usually an Excel sheet or an SQL query. The CSV file will be then used in the application in order to transform it in a search-able format
	* Then the file will be examined in order to identify the data value types and which fields/columns will be used as filters, in order to have the data filtered by these fields’ values. For each filter there are different options depending on the data type, the number of values and what makes sense to be presented to the user
	* The application will then need to have a [Liquid]( https://shopify.github.io/liquid/basics/introduction/) (".liquid") template file provided as model for how the search page will look like. An example is provided which contains all the current possibilities
	* Using these elements, the application will generate a static site with a structure adapted for searching the data, the CSV will be split in smaller blocks, files will be created for value indexes, sort possibilities, keywords and scripts to search them all. The page will be generated with all the filters, sort options and keyword controls
* The actual search will be done from the page
	* The user will select the desired filter values, sort field and direction, the keyword values and then click for a search
	* The script in page will bring (download) the necessary files from the server and execute the search algorithm for the user options. The result data will then be displayed in the page. The example provided with the application includes functionality for pagination and for displaying all the fields in the result. There is another example (Sample 1) which has a personalized result script in order to show the data more closely to a real application

### Getting started

Start by downloading the application. **static-catalog** was developed and tested on Windows and is a Java application

> For the application to start, **Java** should be already installed and functional!

Because it is a Java application it should work fine on Linux and MacOS, but keep in mind that these versions are experimental, unknown problems may be encountered

> In order to start the application on Linux or MacOS the starting script "start-static-catalog.sh" should be manually made executable

After starting the application, the next step is to provide a CSV file. It could be any CSV file with a catalog-like structure, download one from Internet, export an Excel sheet or create it from a database by exporting a table or the result of a query
Then, follow the steps from the documentation in order to get a search page based on the template **static-catalog-dev/static-catalog-page-dev.liquid**

### About

The free components used by this application are listed on the bottom of each page of this site with a link to their respective page/license

This site, the application code, and the static web sites for samples are all hosted at GitHub


