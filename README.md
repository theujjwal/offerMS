# offers-microservice
offers microservice collaborated


# urls

1. http://localhost:9999/offer-service/test GET => test
2. http://localhost:9999/offer-service/offers GET => all offers of all employees
3. http://localhost:9999/offer-service/offers/{offerId} GET => view offer with offerId 
4. http://localhost:9999/offer-service/offers/search/by-category?offerCategory=FURNITURE GET => view offers with category
5. http://localhost:9999/offer-service/offers/search/by-likes?limit=3 GET => view top liked offers
6. http://localhost:9999/offer-service/offers/search/by-creation-date?createdOn=dd-MM-yyyy GET => view offers by posting date
7. http://localhost:9999/offer-service/offers/search/by-author?authorId=subham GET => view offers created by authorId
8. http://localhost:9999/offer-service/offers POST => Create offer
9. http://localhost:9999/offer-service/offers/{offerId}/likes?likedBy=guruSai POST => Like an offer with offerId and likedBy empId
