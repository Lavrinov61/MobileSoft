const admin = require('firebase-admin');
const fs = require('fs');

// IMPORTANT: Path to your service account key file
const serviceAccount = require('./serviceAccountKey.json');

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

const services = JSON.parse(fs.readFileSync('./data/services.json', 'utf8'));
const slots = JSON.parse(fs.readFileSync('./data/schedule_slots.json', 'utf8'));

const importServices = async () => {
  console.log('Starting to import services...');
  const servicesCollection = db.collection('services');
  for (const service of services) {
    const docId = service.id;
    delete service.id; // Remove id from data, as it's the document ID
    await servicesCollection.doc(docId).set(service);
    console.log(`  Imported service: ${docId}`);
  }
  console.log('Services import complete.');
};

const importSlots = async () => {
  console.log('Starting to import schedule slots...');
  const slotsCollection = db.collection('schedule_slots');
  for (const slot of slots) {
    // Convert string dates to Firestore Timestamps
    slot.startTime = admin.firestore.Timestamp.fromDate(new Date(slot.startTime));
    slot.endTime = admin.firestore.Timestamp.fromDate(new Date(slot.endTime));
    await slotsCollection.add(slot);
    console.log(`  Imported a slot for service: ${slot.serviceId}`);
  }
  console.log('Schedule slots import complete.');
};

const runImport = async () => {
  await importServices();
  await importSlots();
  console.log('\nAll data imported successfully!');
};

runImport().catch(console.error);
