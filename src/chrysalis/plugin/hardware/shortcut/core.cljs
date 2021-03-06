;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Gergely Nagy <algernon@madhouse-project.org>
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;;
;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns chrysalis.plugin.hardware.shortcut.core
  (:require [chrysalis.hardware :refer [known?]]
            [re-frame.core :as re-frame]))

(defmethod known? [0x1209 0x2201] [device]
  (assoc device
         :meta {:name "Shortcut v0.4"
                :logo "images/plugins/shortcut-keyboard.png"
                :layout (re-frame/subscribe [:device/svg "images/plugins/shortcut-keyboard.svg"])
                :matrix [14 4]}
         :led {:map [[-1 -1 -1 -1 -1 -1 -1     -1 -1 -1 -1 -1 -1 -1]
                     [-1 -1 -1 -1 -1 -1 -1     -1 -1 -1 -1 -1 -1 -1]
                     [ 1 -1 -1 -1 -1 -1 -1     -1 -1 -1 -1 -1 -1  0]
                     [-1 -1 -1 -1 -1 -1 -1     -1 -1 -1 -1 -1 -1 -1]]}
         :board {:name "Shortcut v0.4"
                 :baud 9600
                 :productId ["0x2201" "0x2200"]
                 :protocol "avr109"
                 :manualReset true
                 :signature (js/Buffer. #js [0x43 0x41 0x54 0x45 0x52 0x49 0x4e])}))
