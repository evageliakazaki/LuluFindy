const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.generateTempPassword = functions.https.onCall(async (data, context) => {
  console.log("📥 Incoming data:", data); // Δεν κάνουμε stringify

  let uid = null;

  if (data && typeof data === "object") {
    if (data.uid) {
      uid = data.uid;
    } else if (data.data && data.data.uid) {
      uid = data.data.uid;
    }
  }

  if (!uid) {
    console.error("❌ UID not found in data.");
    throw new functions.https.HttpsError(
        "invalid-argument",
        "User ID (uid) is required.",
    );
  }

  try {
    const tempPassword = Math.random().toString(36).slice(-8);

    await admin.auth().getUser(uid);
    await admin.auth().updateUser(uid, {
      password: tempPassword,
    });

    console.log(`✅ Temporary password set for UID: ${uid}`);
    return {success: true, tempPassword};
  } catch (error) {
    console.error("❌ Error updating password:", error.message); // Μόνο message
    throw new functions.https.HttpsError("internal",
        "Failed to update user password.",
    );
  }
});

