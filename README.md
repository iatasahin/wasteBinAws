## Access to Firebase:
https://firebase.google.com/docs/admin/setup#initialize_the_sdk_in_non-google_environments

A service account is created from Firebase console. credentials.json is saved.

https://stackoverflow.com/questions/53988485/unable-to-pass-private-key-as-an-environment-variable-in-aws-eb

credentials.json is uploaded to an aws s3 bucket with no public access.

AmazonS3ReadOnlyAccess is granted to aws-elasticbeanstalk-ec2-role from aws console IAM.

resources/.ebextensions/firebase.config is set to copy credentials.json 
from s3 bucket to "/path/to/credentials.json" in the ec2 instance.

environment variable GOOGLE_APPLICATION_CREDENTIALS is set to "/path/to/credentials.json"

## Environment variables:

SPRING_PORT="PORT" 
[5000 for elastic beanstalk]

GOOGLE_APPLICATION_CREDENTIALS="/path/to/credentials.json"

## HTTP endpoints:

### POST /wastebinstatus
saves a waste bin fullness level measurement, example body:

{
  "wasteBin": {
    "id": 12
  },
  "fullnessLevel": 20
}

### POST /wastebin
creates a waste bin, example body:

{
  "id":17,
  "latitude": 28.14306,
  "longitude": 20.7643,
  "filled": false
}

### GET /wastebin/{id}
gets the waste bin with the given id, e.g. GET /wastebin/17

### GET /wastebin/filled
gets the list of waste bins with filled == true

