package com.userdatautilities.DatabaseHelper;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;
/**
 * Created by Ajay on 08/09/18.
 * To run the specific command
 */
@Dao
public interface UserDatabaseQueryDao {

//    fetch All user results
    @Query("SELECT * FROM user")
    List<User> getAll();

    //    getUserSeedIds to fetch specific user results by seed info
    @Query("SELECT * FROM user WHERE seed IN (:seed)")
    List<User> getUserSeedIds(int[] seed);


    //    getUserBySeed to fetch specific user results by seed info
    @Query("SELECT * FROM user WHERE seed LIKE :seed")
    User getUserBySeed(String seed);

    //    getUserByName to fetch specific user results by seed info
    @Query("SELECT * FROM user WHERE name LIKE :name")
    User getUserByName(String name);

    //    getUserByEmail to fetch specific user results by seed info
    @Query("SELECT * FROM user WHERE email LIKE :email")
    User getUserByEmail(String email);

    //    getUserByAge to fetch specific user results by seed info
    @Query("SELECT * FROM user WHERE age LIKE :age")
    User getUserByAge(String age);

    //    getAllUsers  to fetch result list based on query
    @Query("SELECT * FROM user WHERE gender LIKE :gender AND "
            + "seed LIKE :seed LIMIT:Result")
    List<User> getAllUsers(String gender, String seed, int Result );

    //    getAllUsers  to fetch result list based on query gender
    @Query("SELECT * FROM user WHERE gender LIKE :gender LIMIT:result")
    List<User> getUsersBasedOnGender(String gender, int result);


    //    getAllUsers  to fetch result list based on query gender
    @Query("SELECT * FROM user WHERE gender LIKE :gender")
    List<User> getAllUsersBasedOnGender(String gender );

    //    findUsers  to fetch result
    @Query("SELECT * FROM user WHERE gender LIKE :gender AND "
            + "seed LIKE :seed")
    User findUsers(String gender, String seed);


    //    insert user info
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    void insertAll(User... users);

    //    delete user info
    @Delete
    void delete(User user);

    @Query("DELETE FROM user")
    public void nukeTable();
}
