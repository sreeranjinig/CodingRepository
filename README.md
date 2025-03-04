# CodingRepository

## Description

This repository creates a backend RESTful web service for managing packages consisting of one or more products.

## Scope

API supports following:

- Create a package
  * Endpoint - /packages
  * Description - This is for creating new packages. If no currency is specified price defaults to USD.
                 If currency is specified, it retrieves the existing exchange rate from API - https://frankfurter.app/ 
    
- Retrive a package
  * Endpoint -  /packages/{id}
  * Description - This is for retrieving specified package details along with its products.
  
- Update a package - 
  * Endpoint - /packages/{id}
  * Description - This is for updating the existing package by retrieving the package based on the id provided.
                 If no currency is specified price defaults to USD..If currency is specified, it retrieves the existing exchange rate from API - https://frankfurter.app/
  
- Delete a package 
  * Endpoint -  /packages/{id}
  * Description - This is for deleting specified package details along with its products.
  
- List all packages - 
  * Endpoint -  /packages
  * Description - This is for retrieving all packages.

## Improvements
* Adding new product against a package while updating package by id.
* Input Json Schema validation
* Security
