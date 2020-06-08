# VODtest

Android app for testing VOD.

Uses an AppSync GraphQL API to access the DynamoDB table where video 
information and links are stored. The API is secured by Cognito. 

S3 TransferUtility is used to upload videos to the input bucket.