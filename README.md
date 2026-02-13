## 🔊 Breakpoint Sound

**Breakpoint Sound** is an IntelliJ IDEA plugin that plays a customizable sound whenever your debugger hits a breakpoint.

Because sometimes *seeing* the breakpoint isn’t enough — you should **hear it** too.

---

### ✨ Features

- 🔔 Play a sound when a breakpoint is hit  
- 🎯 Create **Custom Sound Breakpoints**  
- 🎵 Choose your own sound file  
- ⚙️ Choose whether sound plays:
  - For **all breakpoints**
  - Or **only for Custom Sound Breakpoints**
- 🖱 Double‑click in the gutter to quickly create a Custom Sound Breakpoint  
- 🧠 Works seamlessly with the IntelliJ debugger  
- 🪶 Lightweight, no performance impact  

---

### 🎯 Custom Sound Breakpoints for Java

Breakpoint Sound introduces a new breakpoint type: **Sound Java Breakpoint**.

This behaves exactly like a normal breakpoint:
- Stops execution
- Supports conditions
- Supports suspend policies
- Works with all standard debugger features

But in addition:
- It plays a sound when hit (depending on your configuration)
- It can be visually distinguished from regular breakpoints

> ⚠ **Note:** Sound Java Breakpoints are currently supported only for **Java debugging (IntelliJ IDEA)**.  
> When using other IDEs such as WebStorm or debugging non-JVM projects, the plugin will still work in **All Breakpoints Mode**, but Custom Sound Breakpoints are not available yet.

---

### 🖱 Quick Creation (Double Click)

You can quickly create a **Sound Java Breakpoint** by:

👉 **Double-clicking in the editor gutter (line numbers area)**

This allows you to mark specific breakpoints as “sound-enabled” without changing your existing workflow.

---

### 🔀 Sound Mode Options

From the settings, you can choose how the plugin behaves:

- 🔊 **All Breakpoints Mode**  
  Plays a sound whenever *any* breakpoint is hit.

- 🎯 **Custom Breakpoints Only Mode**  
  Plays a sound **only** when a Custom Sound Breakpoint is hit.

This gives you full control over how much audio feedback you want during debugging.

---

### 🛠 How It Works

When the debugger pauses execution:

1. The plugin listens for the debug event.
2. It checks your selected sound mode.
3. If the conditions match (all breakpoints or custom-only),
4. The selected sound is played instantly.

You can change the sound or mode at any time — even while debugging.

---

### ⚙️ Configuration

Navigate to:  
**Settings → Tools → Breakpoint Sound**

From there you can:

- Enable or disable sound playback  
- Select a custom sound file  
- Choose between:
  - All Breakpoints
  - Custom Breakpoints Only  
- Adjust other plugin preferences  

> ⚠ Currently, only 3 test sounds are included. A wider variety of sounds will be available soon.

---

### 🧩 Status Bar Widget

The plugin adds a small widget to the **bottom-right corner of the IDE status bar**.

The widget provides:

- Quick visual feedback that Breakpoint Sound is active
- One-click access to toggle sound on or off

This makes it easy to control the plugin without leaving your workflow.

---

### 💡 Use Cases

- Never miss a breakpoint while multitasking  
- Audio feedback during long debug sessions  
- Focus enhancement during multi-window debugging  
- Mark only important breakpoints with sound  

---

### 🚀 Compatibility

- IntelliJ IDEA (Community & Ultimate) – Full support (including Custom Sound Breakpoints)
- Other JetBrains IDEs with debugger support – Sound playback supported in **All Breakpoints Mode**
