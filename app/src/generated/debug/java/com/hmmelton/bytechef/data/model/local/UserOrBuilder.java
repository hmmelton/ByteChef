// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: user.proto

package com.hmmelton.bytechef.data.model.local;

public interface UserOrBuilder extends
    // @@protoc_insertion_point(interface_extends:User)
    com.google.protobuf.MessageLiteOrBuilder {

  /**
   * <code>string id = 1;</code>
   * @return The id.
   */
  java.lang.String getId();
  /**
   * <code>string id = 1;</code>
   * @return The bytes for id.
   */
  com.google.protobuf.ByteString
      getIdBytes();

  /**
   * <code>string display_name = 2;</code>
   * @return The displayName.
   */
  java.lang.String getDisplayName();
  /**
   * <code>string display_name = 2;</code>
   * @return The bytes for displayName.
   */
  com.google.protobuf.ByteString
      getDisplayNameBytes();

  /**
   * <code>string email = 3;</code>
   * @return The email.
   */
  java.lang.String getEmail();
  /**
   * <code>string email = 3;</code>
   * @return The bytes for email.
   */
  com.google.protobuf.ByteString
      getEmailBytes();

  /**
   * <code>repeated string favorite_recipe_ids = 4;</code>
   * @return A list containing the favoriteRecipeIds.
   */
  java.util.List<java.lang.String>
      getFavoriteRecipeIdsList();
  /**
   * <code>repeated string favorite_recipe_ids = 4;</code>
   * @return The count of favoriteRecipeIds.
   */
  int getFavoriteRecipeIdsCount();
  /**
   * <code>repeated string favorite_recipe_ids = 4;</code>
   * @param index The index of the element to return.
   * @return The favoriteRecipeIds at the given index.
   */
  java.lang.String getFavoriteRecipeIds(int index);
  /**
   * <code>repeated string favorite_recipe_ids = 4;</code>
   * @param index The index of the element to return.
   * @return The favoriteRecipeIds at the given index.
   */
  com.google.protobuf.ByteString
      getFavoriteRecipeIdsBytes(int index);

  /**
   * <code>repeated string dietary_restrictions = 5;</code>
   * @return A list containing the dietaryRestrictions.
   */
  java.util.List<java.lang.String>
      getDietaryRestrictionsList();
  /**
   * <code>repeated string dietary_restrictions = 5;</code>
   * @return The count of dietaryRestrictions.
   */
  int getDietaryRestrictionsCount();
  /**
   * <code>repeated string dietary_restrictions = 5;</code>
   * @param index The index of the element to return.
   * @return The dietaryRestrictions at the given index.
   */
  java.lang.String getDietaryRestrictions(int index);
  /**
   * <code>repeated string dietary_restrictions = 5;</code>
   * @param index The index of the element to return.
   * @return The dietaryRestrictions at the given index.
   */
  com.google.protobuf.ByteString
      getDietaryRestrictionsBytes(int index);

  /**
   * <code>repeated string favorite_cuisines = 6;</code>
   * @return A list containing the favoriteCuisines.
   */
  java.util.List<java.lang.String>
      getFavoriteCuisinesList();
  /**
   * <code>repeated string favorite_cuisines = 6;</code>
   * @return The count of favoriteCuisines.
   */
  int getFavoriteCuisinesCount();
  /**
   * <code>repeated string favorite_cuisines = 6;</code>
   * @param index The index of the element to return.
   * @return The favoriteCuisines at the given index.
   */
  java.lang.String getFavoriteCuisines(int index);
  /**
   * <code>repeated string favorite_cuisines = 6;</code>
   * @param index The index of the element to return.
   * @return The favoriteCuisines at the given index.
   */
  com.google.protobuf.ByteString
      getFavoriteCuisinesBytes(int index);
}
