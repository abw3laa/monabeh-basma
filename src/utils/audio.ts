// Web Audio API alarm sound synthesizer for realistic alarm bell and ringtones

let audioCtx: AudioContext | null = null;
let alarmInterval: number | null = null;
let isPlaying = false;

function getAudioContext(): AudioContext {
  if (!audioCtx) {
    const AudioCtxClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
    audioCtx = new AudioCtxClass();
  }
  if (audioCtx.state === 'suspended') {
    audioCtx.resume();
  }
  return audioCtx;
}

export function playAlarmSound(type: string = 'pulse_alarm') {
  if (isPlaying) return;
  isPlaying = true;

  try {
    const ctx = getAudioContext();
    
    // Play dual beep pattern repeating every 1.2 seconds
    const playBeep = () => {
      if (!isPlaying || !ctx) return;
      const now = ctx.currentTime;
      
      const osc1 = ctx.createOscillator();
      const osc2 = ctx.createOscillator();
      const gainNode = ctx.createGain();

      if (type === 'urgent_beep') {
        osc1.type = 'sawtooth';
        osc2.type = 'square';
        osc1.frequency.setValueAtTime(880, now); // A5
        osc2.frequency.setValueAtTime(1760, now + 0.1);
      } else {
        osc1.type = 'triangle';
        osc2.type = 'sine';
        osc1.frequency.setValueAtTime(750, now);
        osc2.frequency.setValueAtTime(950, now + 0.15);
      }

      gainNode.gain.setValueAtTime(0, now);
      gainNode.gain.linearRampToValueAtTime(0.35, now + 0.05);
      gainNode.gain.exponentialRampToValueAtTime(0.001, now + 0.55);

      osc1.connect(gainNode);
      osc2.connect(gainNode);
      gainNode.connect(ctx.destination);

      osc1.start(now);
      osc2.start(now);
      osc1.stop(now + 0.6);
      osc2.stop(now + 0.6);
    };

    playBeep();
    alarmInterval = window.setInterval(playBeep, 900);
  } catch (e) {
    console.error('Audio playback error', e);
  }

  // Trigger vibration if supported
  if ('vibrate' in navigator) {
    try {
      navigator.vibrate([500, 250, 500, 250, 800]);
    } catch {
      // Ignore vibration error on unsupported platforms
    }
  }
}

export function stopAlarmSound() {
  isPlaying = false;
  if (alarmInterval !== null) {
    clearInterval(alarmInterval);
    alarmInterval = null;
  }
  if ('vibrate' in navigator) {
    try {
      navigator.vibrate(0);
    } catch {
      // Ignore
    }
  }
}

export function playHapticSuccess() {
  try {
    const ctx = getAudioContext();
    const now = ctx.currentTime;
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    
    osc.type = 'sine';
    osc.frequency.setValueAtTime(523.25, now); // C5
    osc.frequency.exponentialRampToValueAtTime(1046.50, now + 0.2); // C6
    
    gain.gain.setValueAtTime(0.2, now);
    gain.gain.exponentialRampToValueAtTime(0.001, now + 0.25);
    
    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.start(now);
    osc.stop(now + 0.3);
  } catch {
    // Ignore
  }
}
