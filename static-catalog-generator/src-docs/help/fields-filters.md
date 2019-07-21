## Fields and filters settings

The fields of the input CSV data file will all going to be available in the search page, in order to be used in the results. Some of them will be used to filter the results, and some of them will be used to sort the results. From the previous step it has resulted a **fields-filters** file that is loaded into the tab. This is a JSON file that will be used to generate the search data.

[![Fields and Filters]( ../screenshots/static-catalog--fields-filters.png)]( ../screenshots/static-catalog--fields-filters.png)

The setting of a field can be edited, and it can be made to be a sort field (ascending or descending) and/or to be a filter field. For the sort fields a couple of labels could be also specified for ascending and descending options, to be used on the page as friendly names.

[![Edit Field]( ../screenshots/static-catalog--edit-field.png)]( ../screenshots/static-catalog--edit-field.png)

The filter types could be:

### "Values" filter

This filter is made up from all the distinct values of the field, and when multiple values are selected, the union of the lines with those values will be selected

### "Value intervals" filter

This filter can be used for numeric (integer and real) fields having a big number of values, where the individual values cannot be practically displayed and should be arranged into intervals. By specifying an "interval value", the field distinct values will be grouped in intervals by the multiples of that value. For example, if the interval value is 1000, all values between 0 and 999 will be in the first interval, al values between 1000 and 1999 will be in the second interval etc.

### "Length intervals" filter

This filter can be used for any data type fields having a large number of values where the individual values cannot be practically displayed and should be arranged into intervals. By specifying an "interval value", the field distinct values will be grouped in intervals by counting them to be that number count in each interval. For example, if the interval value is 100, the first interval will contain the first 100 values, the second one the next 100 values etc.

For these value filters, the following settings are available to be used in displaying a filter in the page (for intervals the filter values will be the beginning and the end field values of the interval separated by a "-"):

It can be displayed in the page as a collection of checkboxes (in this case multiple values can be selected) or as a dropdown or a collection of radio buttons, and in this case only one value can be selected.
It will contain two options about how to be displayed in page, for checkboxes and radio buttons, how many values can be displayed for the filter until making a "more..." like button, and when are more than that number of values, how many to display above the "more..." option.

For example, if there are 20 filter values and the first ones are more used, it makes sense to display only 5 and if the user wants the other 15 to click on the "more..." option. However, if the values are equally used, for example the month of the year, or the days of the month, would be better not to break it but display all 12 (respectively 31) filter values.

In all cases the exception values (if exists) will be placed first and marked as exceptions (to be displayed differently – on italic for example).

There is also a filter setting to apply a format to the displayed filter values, can be useful for dates, for display them more friendly

Another filter setting allows for the values to be replaced when displayed with other values, for example "Y" replaced with "Yes", "N" replaced with "No", "N/A" replaced with "Not available". This setting is very useful for a small number of clearly known values, but it should not be used in place of having the values in the CSV. So, if it is possible to have the values already replaced in the source CSV, that is the best way.

Finally, the data type of a field filter can be changed from here. This should be done only if in certain cases the numeric values detected by the application need to be treated as text values.

### "Keywords" filter

This filter will take all the field values and make them search-able by the starting letters. It can only be displayed in page as a "Search Box" options.

The search will work in two phases, when one letter is typed (provided as input) all possible two letter combinations starting with that letter and possible for the field values will be provided (displayed). When two letters are chosen, all the possible field values starting with those two letters will be provided. From that point for each more letter the number of values will be reduced to contain only the ones starting with the letters.

Once satisfied with the sort and filters, save **fields-filters** file and proceed to the catalog generation in the next tab.
