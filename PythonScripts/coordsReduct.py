from pykml import parser
from pykml.helpers import set_max_decimal_places
from lxml import etree
from os import path
import html

import database

con = database.connect_db()
cursor = con.cursor()
provincia = ""

kml_file_path = 'kml/esp.kml'
kml_file = path.join('kml/pueblos.kml')

# Parse the input file into an object tree
with open(kml_file, encoding="utf8") as f:
    tree = parser.parse(f)

# Get a reference to the "Document.Folder" node
folder = tree.getroot().Document.Folder
# Limit the decimals of the coordinates
set_max_decimal_places(folder, max_decimals={
    'longitude': 2,
    'latitude': 2,
})

# We go through the sites to put them in the database
for place in folder.Placemark:
    cont = 0
    for data in place.ExtendedData.SchemaData.SimpleData:
        if cont == 2:
            # If the province is different from the previous ones,
            # it adds it to the database and obtains its ID
            if provincia != data.text:
                provincia = data.text
                query = "INSERT INTO PROVINCIAS (pr_name, pr_pais) VALUES ('{}', 1)".format(provincia)
                cursor.execute(query)
                idProvincia = cursor.lastrowid

        if cont == 4:
            ciudad = data.text
        cont = cont + 1

    coords = place.MultiGeometry.Polygon.outerBoundaryIs.LinearRing.coordinates
    query = "INSERT INTO CIUDADES (ci_name, ci_provin, ci_coord) VALUES ('{}', {}, '{}')".format(
        html.escape(ciudad), int(idProvincia), coords)
    cursor.execute(query)

con.commit()

#print(folder.Placemark.ExtendedData.SchemaData.SimpleData.text)