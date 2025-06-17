package com.example.lulufindy;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeviceId {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public void verifyDeviceId(String userId, String currentDeviceId, final DeviceCheckCallback callback) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String storedDeviceId = document.getString("deviceId");
                                if (storedDeviceId != null && storedDeviceId.equals(currentDeviceId)) {
                                    callback.onResult(true);
                                } else {
                                    callback.onResult(false);
                                }
                            } else {
                                callback.onResult(false);
                            }
                        } else {
                            callback.onError(task.getException());
                        }
                    }
                });
    }


    public interface DeviceCheckCallback {
        void onResult(boolean isValid);
        void onError(Exception e);
    }
}
