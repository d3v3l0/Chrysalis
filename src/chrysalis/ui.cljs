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

(ns chrysalis.ui
  (:require [clojure.string :as s]
            [garden.core :as g]
            [garden.units :as gu]

            [chrysalis.core :as core]
            [chrysalis.settings :as settings]
            [chrysalis.device :as device]

            [chrysalis.ui.about :refer [<about>]]))

(def mousetrap (js/require "mousetrap"))

;;; ---- Page ---- ;;;

(defmulti page
  (fn [action p]
    [action p]))

(defmethod page :default [_ _ _])

(defn current-page []
  (:page @core/state))

(defn switch-to-page! [p]
  (page :leave (current-page))
  (.reset mousetrap)
  (page :enter p)
  (swap! core/state assoc :page p))

;;; ---- Menu ---- ;;;

(defn- <menu-item> [state [key meta] index]
  (let [disabled? (and (:disable? meta)
                       ((:disable? meta)))
        current? (= key (current-page))]
    (.bind mousetrap (str "alt+" index) (fn [& _]
                                          (when-not (and (:disable? meta)
                                                         ((:disable? meta)))
                                            (switch-to-page! key))))
    [:a.dropdown-item {:href "#"
                       :key (str "main-menu-" (name key))
                       :class (when (or disabled? current?) "disabled")
                       :on-click (fn [e]
                                   (.preventDefault e)
                                   (when-not disabled?
                                     (switch-to-page! key)))}
     (if current?
       [:b (:name meta)]
       (:name meta))

     [:div.text-right.text-mute {:style {:float :right}} "Alt+" index]]))

(defn <main-menu> [state pages]
  [:nav.navbar.navbar-toggleable-md.navbar-inverse.bg-inverse.fixed-top
   [:button.navbar-toggler.navbar-toggler-right {:type :button
                                                 :data-toggle :collapse
                                                 :data-target "#navbarSupportedContent"}
    [:span.navbar-toggler-icon]]
   [:div.dropdown
    [:a.navbar-brand.link-button.text-white {:data-toggle :dropdown
                                             :href "#"}
     [:i.fa.fa-spinner] " Chrysalis: " [:b (-> (current-page)
                                               pages
                                               :name)]]
    [:div.dropdown-menu {:id "main-menu"}
     (doall (for [[index menu-item] (map-indexed vector (sort-by (fn [[key meta]] (:index meta)) pages))]
              (<menu-item> state menu-item index)))
     [:hr]
     [:a.dropdown-item {:href "#about"
                        :data-toggle :modal} "About"]
     [:a.dropdown-item {:href "#"
                        :on-click #(.close js/window)} "Quit"
      [:div.text-right.text-mute {:style {:float :right}} "Ctrl+Q"]]]]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}]
   [:span.navbar-text {:style {:white-space :pre}}
    (when-let [device (device/current)]
      (get-in device [:device :meta :name]))]])

;;; ---- Helpers ---- ;;;

(defn style

  ([styles]
   [:style {:type "text/css"}
    (g/css styles)])

  ([]
   (style [:#chrysalis {:margin-top (gu/rem 5)}
           [:#main-menu {:min-width (gu/rem 20)}]
           [:.navbar-brand:hover
            [:i {:animation "fa-spin 2s infinite linear"}]]
           [:.link-button {:color "#292b2c"}]])))

(defn- toHex [i]
  (let [hex (.toString i 16)]
    (if (< i 16)
      (str "0" hex)
      hex)))

(defn color->hex [color]
  (str "#" (apply str (map toHex color))))

;;; ---- Main application ---- ;;;
(defn chrysalis []
  [:div
   [style]
   [<about>]
   [<main-menu> @core/state @core/pages]
   [:div {:id :page}
    (page :render (current-page))]])
