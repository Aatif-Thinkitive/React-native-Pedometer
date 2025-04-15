import React, { useEffect, useState } from 'react';
import { Button, Text, View, NativeModules, DeviceEventEmitter } from 'react-native';

const { Pedometer } = NativeModules;

export default function App() {
  const [steps, setSteps] = useState(0);

  const fetchSteps = async () => {
    const result = await Pedometer.getCurrentStepData();
    setSteps(result.steps);
  };

  useEffect(() => {
    const interval = setInterval(() => {
      fetchSteps();
      console.log("Steps:" + steps);
      
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text style={{ fontSize: 24, marginBottom: 20 }}>Steps: {steps}</Text>

      <Button title="Start Step Counter" onPress={() => Pedometer.startStepService() } />
      <View style={{ height: 16 }} />
      <Button title="Stop & Reset Counter" onPress={() => {
        Pedometer.stopStepService();
        setSteps(0);
      }} />
    </View>
  );
}
