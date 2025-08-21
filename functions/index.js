const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");

admin.initializeApp();
const db = admin.firestore();

// IMPORTANT: The user must configure these in the Cloud Functions environment
// using `firebase functions:config:set cloudpayments.private_api_key="YOUR_KEY"`
const cloudPaymentsApiKey = functions.config().cloudpayments.private_api_key;

/**
 * Creates a payment invoice/charge with CloudPayments.
 * This function is callable from the client app.
 *
 * @param {object} data - The data passed from the client.
 * @param {string} data.serviceId - The ID of the service to be purchased.
 * @param {string} data.userId - The ID of the user making the purchase.
 *
 * @returns {Promise<object>} A promise that resolves with the data from CloudPayments
 *                            needed to initialize the SDK on the client.
 */
exports.createPaymentInvoice = functions.https.onCall(async (data, context) => {
  // Check for authentication
  if (!context.auth) {
    throw new functions.https.HttpsError(
        "unauthenticated",
        "The function must be called while authenticated.",
    );
  }

  const { serviceId } = data;
  const userId = context.auth.uid;

  if (!serviceId) {
    throw new functions.https.HttpsError(
        "invalid-argument",
        "The function must be called with a 'serviceId'.",
    );
  }

  try {
    // 1. Get the service details from Firestore to get the real price
    const serviceDoc = await db.collection("services").doc(serviceId).get();
    if (!serviceDoc.exists) {
      throw new functions.https.HttpsError("not-found", "Service not found.");
    }
    const service = serviceDoc.data();
    const amount = service.price;

    // 2. Call the CloudPayments API to create a charge/invoice
    // NOTE: This is a MOCKED example of the CloudPayments API call.
    // The actual endpoint and payload will depend on their documentation.
    const cloudPaymentsResponse = await axios.post(
        "https://api.cloudpayments.com/payments/cards/charge", // Example endpoint
        {
          Amount: amount,
          Currency: "RUB", // Example currency
          Description: `Booking for ${service.name}`,
          AccountId: userId,
          // Other required parameters like IpAddress, etc.
        },
        {
          headers: {
            "Authorization": `Basic ${Buffer.from(cloudPaymentsApiKey + ":").toString("base64")}`,
            "Content-Type": "application/json",
          },
        },
    );

    // 3. Return the necessary data to the client
    // This will likely include a transaction ID or some other token
    // that the client-side SDK needs to proceed.
    return {
      success: true,
      transactionId: cloudPaymentsResponse.data.Model.TransactionId, // Example response path
    };
  } catch (error) {
    console.error("Payment invoice creation failed:", error);
    throw new functions.https.HttpsError(
        "internal",
        "Failed to create payment invoice.",
    );
  }
});
