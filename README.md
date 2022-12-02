# JOmeZarr

A Java library for reading OME-Zarr datasets.

This library is currently incomplete with functionality implemented as required by consumers.

## General Information

JOmeZarr handles the additional OME-Zarr specific `.zgroup` layer that specifies the set of underlying Zarr arrays.  The actual reading of Zarr data is done by one of the existing Zarr libraries.  JOmeZarr currently integrates [JZarr](https://github.com/bcdev/jzarr), however any library that provides basic chunked reading for Zarr files could be used.

The default compression for many Zarr libraries is [Blosc](https://github.com/Blosc).  An implementation of this compressor must be on the library path for any executable using JOmeZarr.  On some platforms it is installed by default or can be added with a package manager.  For others, it may be necessary to compile or download prebuilt binaries and add the location to the library path.

## Testing
Some test use the [OME-Zarr prototypes samples](https://github.com/ome/ome-ngff-prototypes) (v0.4).  A `TestSampleDir` Java property must be used when running test with a valid location for those tests to be included.