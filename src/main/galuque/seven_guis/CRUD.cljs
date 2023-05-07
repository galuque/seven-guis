(ns galuque.seven-guis.CRUD
  (:require [uix.core :as uix :refer [defui $]]
            [clojure.string :as str :refer [lower-case capitalize]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))

(def initial-state {:prefix ""
                    :id nil
                    :first-name ""
                    :last-name  ""
                    :users {1 {:id 1
                               :first-name "john"
                               :last-name "doe"}
                            2 {:id 2
                               :first-name "jane"
                               :last-name "doe"}}})

(defn inputs-empty? [{:keys [first-name last-name]}]
  (or (empty? first-name) (empty? last-name)))

(defn user-str [user]
  (str (capitalize (:last-name user)) ", " (capitalize (:first-name user))))

(defn same-name? [users first-name last-name]
  (some #(and (= (:first-name %) (lower-case first-name))
              (= (:last-name %) (lower-case last-name)))
        (vals users)))

(defn known-user? [{:keys [id users first-name last-name]}]
  (or id
       (same-name? users first-name last-name)))

(defn filter-pattern [{:keys [prefix]}]
  (re-pattern (str "(?i)" prefix)))

(defn user-visible? [pattern user]
  (re-find pattern (:last-name user)))

(defmulti crud-reducer (fn [_ action] (:type action)))

(defmethod crud-reducer :prefix [state {:keys [event]}]
  (assoc state :prefix (.. event -target -value)))

(defmethod crud-reducer :first-name [state {:keys [event]}]
  (assoc state :first-name (.. event -target -value)))

(defmethod crud-reducer :last-name [state {:keys [event]}]
  (assoc state :last-name (.. event -target -value)))

(defmethod crud-reducer :select [{:keys [id] :as state} {:keys [event]}]
  (let [selected-id (-> event .-target .-value js/parseInt)]
    (if (= id selected-id)
      (assoc state :id nil :first-name "" :last-name "")
      (let [user (get-in state [:users selected-id])
            {:keys [first-name last-name]} user]
        (assoc state :id selected-id :first-name  (capitalize first-name) :last-name (capitalize last-name))))))

(defmethod crud-reducer :clear [state _]
    (assoc state :id nil :first-name "" :last-name ""))

(defmethod crud-reducer :create [{:keys [first-name last-name users] :as state} {:keys [id-ref]}]
  (let [next-id (swap! id-ref inc)
        user {:id next-id
              :first-name (lower-case first-name)
              :last-name (lower-case last-name)}]
    (assoc state :users (assoc users next-id user) :id nil :first-name "" :last-name "")))

(defmethod crud-reducer :update [{:keys [id first-name last-name] :as state} _] 
  (-> state
      (assoc-in [:users id] {:id id :first-name (lower-case first-name) :last-name (lower-case last-name)})
      (assoc :id nil :first-name "" :last-name "")))

(defmethod crud-reducer :delete [{:keys [id users] :as state} _]
    (assoc state :users (dissoc users id) :id nil :first-name "" :last-name ""))

(defui CRUD []
  (let [[state dispatch!] (uix/use-reducer crud-reducer initial-state)
        id-ref (uix/use-ref 2)]
    ($ centered-box
       ($ :div.div.flex.flex-col.border-2.border-stone-400.rounded-lg.p-4.m-4
          ($ :input.m-4.border-2.border-stone-200.rounded-lg 
             {:type "text"
              :placeholder "Filter by last name"
              :value (:prefix state)
              :on-change #(dispatch! {:type :prefix :event %})})
          ($ :div.flex.flex-col.border-2.border-stone-200.rounded-lg
             ($ :label {:for "users"} "Users")
             ($ :select#users.checked:bg-gray-100
                {:name "users"
                 :size 10
                 :multiple true
                 :value [(if (:id state) (:id state) "")]
                 :on-change #(dispatch! {:type :select :event %})}
                ($ :option
                   {:value ""
                    :disabled true
                    :on-click #(dispatch! {:type :clear})}
                   "Select a user...")
                (for [userv (:users state)
                      :let [user (get userv 1)
                            id (:id user)
                            pattern (filter-pattern state)]
                      :when (user-visible? pattern user)]
                  ($ :option.hover:bg-gray-300.checked:bg-gray-100
                     {:key id
                      :value id}
                     (user-str user))))))
       ($ :div.flex.flex-col
          ($ :input.m-4..p-4.border-2.border-stone-200.rounded-lg
             {:type "text"
              :placeholder "First Name"
              :value (:first-name state)
              :on-change #(dispatch! {:type :first-name :event %})})
          ($ :input.m-4.p-4.border-2.border-stone-200.rounded-lg
             {:type "text"
              :placeholder "Last Name"
              :value (:last-name state)
              :on-change #(dispatch! {:type :last-name :event %})})
          ($ button
             {:disabled (or (inputs-empty? state) (known-user? state))
              :on-click #(dispatch! {:type :create :id-ref id-ref})}
             "Create")
          ($ button
             {:disabled (or (inputs-empty? state) (not (:id state)))
              :on-click #(dispatch! {:type :update})}
             "Update")
          ($ button
             {:disabled (or (inputs-empty? state) (not (:id state)))
              :on-click #(dispatch! {:type :delete})}
             "Delete")
          ($ button
             {:disabled (and (empty? (:first-name state))
                             (empty? (:last-name state)))
              :on-click #(dispatch! {:type :clear})}
             "Clear")))))
