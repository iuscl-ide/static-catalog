## Generate the Catalog Files

The catalog generation is the last step in having a search-able catalog of static data. It needs to take the settings made in the previous step and apply it to the CSV data in order to produce the structure of files that can be downloaded and processed on the browser.

There are four necessary elements that have to be provided to the generate catalog engine


### The **source** CSV data file

This is the initial file, it can be viewed in the first tab and examined in the second tab

### The **fields-filters** file

This is the settings file which was created (edited) in the third tab during the "Fields and Filters" step

### The **template** file

The generation step will create several files and folders, inside a root folder (usually named result), on the following structure:

_catalog

This folder contains the data necessary for the search. Normally its content should never be modified after it is created. For information purposes only, here is its content:

Data

Here is the source CSV data, split into pieces, each one less than one megabyte

Indexes

Here are the values for values and interval indexes. There is one file per filter (field)

Keywords

The keywords filters are kept here as a folder for each filter and a file for each two letters which are the begging of a word

Scripts

This folder contains the scripts that do the actual search in the data. These scripts should not be modified.

Sort

The data sorts are kept here as one per field and direction (so maximum two per field – ascending and descending)


Again, this _catalog folder should be taken as it is, it will be re-generated (deleted, re-created and written) at each generate operation

Another generated folder is:

_catalog-page

This folder contains the search data which should be used in the html page, in order to display to the user the search options. It will contain:

Keywords

This folder will contain the possible values (words) of the keyword filters
The structure is one folder per filter and inside it one file for each two letters pair which is the beginning of a keyword. Each file will contain all the words starting with a pair and for each word the number (counter) of lines (results) it appears.
These files can be used by specially written page controls to display to the user the possible words while the first letters are typed.
A complete example is given for the Search Box control (see below)

static-catalog-page.json

This file is the main setting file for the entire search page. It contains all the fields, and for each filed the settings from the file **fields-filters**, and if the field is a filter all possible filter values and for each value the number (counter) of lines (results) it appears on.

Is this file that have to be used to show the filters inside the page in order to allow the user to do a selection for search

[![Generate Catalog]( ../screenshots/static-catalog--generate.png)]( ../screenshots/static-catalog--generate.png)

This file offers all the necessary data in order to create the search controls, the way it will be used is up to the developer. It can be used in the page to be used by a script that will dynamically create the controls, it could be used for already created controls to be filled with data, or it can be used as an entry for a templating system. By default Static-Catalog supports the Liquid template system, if a liquid file is provided the application will execute the directives inside on the static-catalog-page.json file.
A complete example for such a file is provided in the folder static-catalog-dev in the file static-catalog-page-dev.liquid
This file is part of a complete example with a style library (Semantic-UI) and own style/scripts



The source CSV file is made up of columns (fields) of data, each one having a specific data type. In order to find the data types, the file should be loaded in the second tab of the application and examined. Once examined, it will show in the result grid on the first column the name of each field, taken from the first line of the file. Then on the second column the type of the field.

The type of the field is found by analyzing the data. However, we can have all the values of a field beeing the same data type, EXCEPT for a small number of them representing something else. The most frequent example is when the value is empty, it will be treated as an exception of the rest of the data. Another example is the value "NULL" or "N/A" (not available) or "All" or "none", any of these can be treated as exception if the rest of the data have the same type. So, the first thing to do is set the maximum number of such distinct values that can be found and considered exceptions, by default the number is 1.

Considering as "values" all the rest of the non-exception values found for a field, the types will be set as follows:

1. If all the values can be converted to date, then the field type is "Date".
2. If all the values can be converted to double, then the field type is "Real".
3. If all the values can be converted to an integer, then the field type is "Integer".
4. If the values cannot be all converted to one of the above, the type is "Text".

[![Examine CSV]( ../screenshots/static-catalog--examine-csv.png)]( ../screenshots/static-catalog--examin-csv.png)

In order to use a field as a filter, the number of its distinct values must be known and will be displayed next. In order for a filter to be used by a user it has to have a reduced number of values, usually less than 100 (default 50) because more would be difficult to understand. For the fields with less distinct values than the maximum filter values, the values will be displayed next with the count for each value. First will be displayed the exceptions (if exists – and will always be the first on the filter) and then all the values (double click to see them all).

> Here and in the rest of **static-catalog** unless specifically stated otherwise the values will be sorted by "natural-sorting", this means that numbers will be considered numbers even inside the words ("a2" will be before "a10").

By changing the number of exceptions and maximum filter values, the file can be examined to see the most appropriate way to have value filters. Where in doubt, go back to the previous tab and see how the file looks like.

In order to help the page generation in the next steps, here can be set two global parameters about how many values can be shown to the user, and if there are more values how many values to show until have a "more" button or a similar function. These values can be changed for each field in the next step.

Once satisfied with the examination, by pressing "Create New Filters" a **fields-filters** file will be created and opened in the next tab.
