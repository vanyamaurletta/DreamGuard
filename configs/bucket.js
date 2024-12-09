const { Storage } = require('@google-cloud/storage');
require('dotenv').config();

const storage = new Storage();

const bucketName = process.env.CLOUD_STORAGE_BUCKET;
const bucket = storage.bucket(bucketName);

module.exports = { bucket };