(function() {
  'use strict';

  let touchLookId = null;
  let lastTouchX = 0;
  let lastTouchY = 0;

  window.__luckyMobile = {
    sensitivityX: 1.6,
    sensitivityY: 1.6,
    invertY: false,
    initialized: false,

    init: function() {
      if (this.initialized) return;
      this.initialized = true;

      // Register touch listeners for smooth canvas camera looking
      this.setupTouchLook();

      const checkInterval = setInterval(() => {
        if (window.__game && window.__game.game) {
          clearInterval(checkInterval);
          this.onGameReady();
        }
      }, 100);
    },

    setupTouchLook: function() {
      const isInteractive = (el) => {
        if (!el) return false;
        const tag = el.tagName ? el.tagName.toLowerCase() : '';
        if (tag === 'button' || tag === 'input' || tag === 'select' || tag === 'textarea') return true;
        if (el.closest && (el.closest('.mmodal') || el.closest('.inv-root') || el.closest('#ui-root > *'))) {
          return true;
        }
        return false;
      };

      window.addEventListener('touchstart', (e) => {
        for (let i = 0; i < e.changedTouches.length; i++) {
          const t = e.changedTouches[i];
          // Look zone: right 65% of screen
          if (touchLookId === null && t.clientX > window.innerWidth * 0.35) {
            const target = document.elementFromPoint(t.clientX, t.clientY);
            if (isInteractive(target)) continue;
            touchLookId = t.identifier;
            lastTouchX = t.clientX;
            lastTouchY = t.clientY;
          }
        }
      }, { passive: true });

      window.addEventListener('touchmove', (e) => {
        for (let i = 0; i < e.changedTouches.length; i++) {
          const t = e.changedTouches[i];
          if (t.identifier === touchLookId) {
            const dx = t.clientX - lastTouchX;
            const dy = t.clientY - lastTouchY;
            lastTouchX = t.clientX;
            lastTouchY = t.clientY;
            this.look(dx, dy);
          }
        }
      }, { passive: true });

      const onTouchEnd = (e) => {
        for (let i = 0; i < e.changedTouches.length; i++) {
          if (e.changedTouches[i].identifier === touchLookId) {
            touchLookId = null;
          }
        }
      };

      window.addEventListener('touchend', onTouchEnd, { passive: true });
      window.addEventListener('touchcancel', onTouchEnd, { passive: true });
    },

    onGameReady: function() {
      const g = window.__game.game;
      const a = window.__game.audio;

      // Hook into audio SFX to trigger Android tactile haptics
      if (a && a.sfx) {
        const originalSfx = a.sfx.bind(a);
        a.sfx = function(name, opts) {
          try {
            if (window.AndroidBridge && typeof window.AndroidBridge.onSfx === 'function') {
              window.AndroidBridge.onSfx(String(name));
            }
          } catch(e) {}
          return originalSfx(name, opts);
        };
      }

      // Periodically sync player stats to Android
      setInterval(() => {
        this.syncState();
      }, 120);

      // Notify Android Bridge that game engine is loaded and active
      if (window.AndroidBridge && typeof window.AndroidBridge.onEngineReady === 'function') {
        window.AndroidBridge.onEngineReady();
      }
    },

    syncState: function() {
      if (!window.__game || !window.__game.game) return;
      try {
        const p = window.__game.game.player;
        const inv = window.__game.game.inventory;
        if (p && window.AndroidBridge && typeof window.AndroidBridge.onPlayerState === 'function') {
          const state = {
            hp: Math.round(p.health ?? 20),
            maxHp: Math.round(p.maxHealth ?? 20),
            hunger: Math.round(p.hunger ?? 20),
            x: Math.round(p.x ?? 0),
            y: Math.round(p.y ?? 0),
            z: Math.round(p.z ?? 0),
            dead: Boolean(p.dead),
            onGround: Boolean(p.onGround),
            selectedSlot: (inv && typeof inv.selectedIndex === 'number') ? inv.selectedIndex : 0
          };
          window.AndroidBridge.onPlayerState(JSON.stringify(state));
        }
      } catch(e) {}
    },

    // Movement: forward, back, left, right (booleans)
    setMovement: function(forward, back, left, right) {
      if (!window.__game || !window.__game.game) return;
      const keys = window.__game.game.input.keys;
      forward ? keys.add("KeyW") : keys.delete("KeyW");
      back ? keys.add("KeyS") : keys.delete("KeyS");
      left ? keys.add("KeyA") : keys.delete("KeyA");
      right ? keys.add("KeyD") : keys.delete("KeyD");
    },

    setJump: function(down) {
      if (!window.__game || !window.__game.game) return;
      const keys = window.__game.game.input.keys;
      down ? keys.add("Space") : keys.delete("Space");
    },

    setSprint: function(down) {
      if (!window.__game || !window.__game.game) return;
      const keys = window.__game.game.input.keys;
      down ? keys.add("ControlLeft") : keys.delete("ControlLeft");
    },

    setSneak: function(down) {
      if (!window.__game || !window.__game.game) return;
      const keys = window.__game.game.input.keys;
      down ? keys.add("ShiftLeft") : keys.delete("ShiftLeft");
    },

    // Mining / Attacking (Left Click)
    setMine: function(down) {
      if (!window.__game || !window.__game.game) return;
      const input = window.__game.game.input;
      input.leftDown = !!down;
      if (down) {
        input.leftClicks++;
      }
    },

    // Placing / Using / Interacting (Right Click)
    setPlace: function(down) {
      if (!window.__game || !window.__game.game) return;
      const input = window.__game.game.input;
      input.rightDown = !!down;
      if (down) {
        input.rightClicks++;
      }
    },

    // Camera Look: dx, dy from touch swipe
    look: function(dx, dy) {
      if (!window.__game || !window.__game.game) return;
      const p = window.__game.game.player;
      if (p && typeof p.applyLook === 'function') {
        const multY = this.invertY ? -this.sensitivityY : this.sensitivityY;
        p.applyLook(dx * this.sensitivityX, dy * multY);
      }
    },

    // Hotbar selection 1 through 9
    selectHotbar: function(slotNum) {
      const code = 'Digit' + slotNum;
      window.dispatchEvent(new KeyboardEvent('keydown', { code: code, bubbles: true }));
      setTimeout(() => {
        window.dispatchEvent(new KeyboardEvent('keyup', { code: code, bubbles: true }));
      }, 30);
    },

    // UI actions
    openInventory: function() {
      window.dispatchEvent(new KeyboardEvent('keydown', { code: 'KeyE', bubbles: true }));
      setTimeout(() => {
        window.dispatchEvent(new KeyboardEvent('keyup', { code: 'KeyE', bubbles: true }));
      }, 30);
    },

    openQuests: function() {
      window.dispatchEvent(new KeyboardEvent('keydown', { code: 'KeyJ', bubbles: true }));
      setTimeout(() => {
        window.dispatchEvent(new KeyboardEvent('keyup', { code: 'KeyJ', bubbles: true }));
      }, 30);
    },

    openCodex: function() {
      window.dispatchEvent(new KeyboardEvent('keydown', { code: 'KeyK', bubbles: true }));
      setTimeout(() => {
        window.dispatchEvent(new KeyboardEvent('keyup', { code: 'KeyK', bubbles: true }));
      }, 30);
    },

    dropItem: function(wholeStack) {
      window.dispatchEvent(new KeyboardEvent('keydown', { code: 'KeyQ', shiftKey: !!wholeStack, bubbles: true }));
      setTimeout(() => {
        window.dispatchEvent(new KeyboardEvent('keyup', { code: 'KeyQ', shiftKey: !!wholeStack, bubbles: true }));
      }, 30);
    },

    interactWagon: function() {
      window.dispatchEvent(new KeyboardEvent('keydown', { code: 'KeyF', bubbles: true }));
      setTimeout(() => {
        window.dispatchEvent(new KeyboardEvent('keyup', { code: 'KeyF', bubbles: true }));
      }, 30);
    },

    saveGame: function() {
      if (window.__game && typeof window.__game.saveNow === 'function') {
        window.__game.saveNow();
      }
    },

    unstuck: function() {
      if (window.__game && window.__game.game && window.__game.game.player) {
        window.__game.game.player.y += 3.5;
        window.__game.game.player.vy = 2.0;
      }
    },

    setSensitivity: function(val) {
      this.sensitivityX = val;
      this.sensitivityY = val;
    },

    setInvertY: function(val) {
      this.invertY = !!val;
    }
  };

  // Start initialization immediately
  window.__luckyMobile.init();
})();
