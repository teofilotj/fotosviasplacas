import React, { useState, useEffect } from "react";
import { View, Text, Button, Image, TextInput, Alert } from "react-native";
import * as ImagePicker from "expo-image-picker";
import * as Location from "expo-location";
import * as MailComposer from "expo-mail-composer";

export default function App() {
  const [image, setImage] = useState(null);
  const [location, setLocation] = useState(null);
  const [problemType, setProblemType] = useState("");

  useEffect(() => {
    (async () => {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        Alert.alert("Permissão negada", "Localização é necessária.");
        return;
      }

      let loc = await Location.getCurrentPositionAsync({});
      setLocation(loc);
    })();
  }, []);

  const pickImage = async () => {
    let result = await ImagePicker.launchCameraAsync({
      quality: 0.5,
      base64: false,
    });

    if (!result.canceled) {
      setImage(result.assets[0].uri);
    }
  };

  const sendReport = async () => {
    if (!problemType || !image || !location) {
      Alert.alert("Erro", "Complete todos os campos antes de enviar.");
      return;
    }

    const locationText = `Latitude: ${location.coords.latitude}, Longitude: ${location.coords.longitude}`;

    const options = {
      recipients: ["ouvidoria@fortaleza.ce.gov.br"],
      subject: `Relato de Problema - ${problemType}`,
      body: `Tipo de Problema: ${problemType}\nLocalização: ${locationText}`,
      attachments: [image],
    };

    let isAvailable = await MailComposer.isAvailableAsync();
    if (isAvailable) {
      await MailComposer.composeAsync(options);
    } else {
      Alert.alert("Erro", "Serviço de email não disponível.");
    }
  };

  return (
    <View className="flex-1 items-center justify-center bg-white p-4">
      <Text className="text-xl font-bold mb-4">Aldeota Cidadã</Text>

      <Button title="Tirar Foto do Problema" onPress={pickImage} />
      {image && <Image source={{ uri: image }} style={{ width: 200, height: 200, marginTop: 10 }} />}

      <TextInput
        className="border mt-4 p-2 w-full"
        placeholder="Tipo de problema (ex: buraco, calçada quebrada)"
        value={problemType}
        onChangeText={setProblemType}
      />

      <Button title="Enviar Relato" onPress={sendReport} className="mt-4" />
    </View>
  );
}