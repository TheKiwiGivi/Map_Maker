# Map_Maker

# Purpose
This app is used to create map files for a game called Key Labyrinth. 
The actual output of such a file is fairly simple. However the advantage of using this app is that it's much easier to create a map if you can actually see it being made in real time with pictures (as opposed to just writing numbers in a text file).

# How to use
You're first presented with a menu. Here you can choose to change settings, load previously made maps, or create new maps.

# Settings
The only setting available is a 'sharedpreference' which lets you set default map dimensions when creating new maps.
Write your new default map dimensions and hit the 'Save prefs' button to save new preferences.

# Load map
This part presents you a list of all maps currently available to edit. You select a map by clicking on it, then confirm by pressing the 'Select' button.
You can also delete maps by clicking the garbage icon.
When map is loaded, you are allowed to change the tiles and map size, but it will only be saved if you click the 'Update' button.

# Create map
This is where new maps are created. You start off with empty tiles in the dimensions specified in the default values.
These dimensions can be changed by specifying the new dimensions in the text fields and clicking the 'Refresh' button.
You can also refresh the map, not changing the dimensions but just making all tiles empty by clicking the 'Refresh' button without specifying new dimensions.
To save your masterpiece, click the save button and preferably specify a name. Please be cautious that the text 'map_' will be added infront of your file name. This is to make it fit with the game logic of the game to read map files.

# Errors
If you get an error trying to run the app, please try clicking 'Build' -> 'Rebuild project'.