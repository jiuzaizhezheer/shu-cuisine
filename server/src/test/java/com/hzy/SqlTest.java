package com.hzy;

public class SqlTest {
    //TODO 在数据库批量查询中整合，返回List<Double>
    /**
     * <select id="sumByMap" resultType="java.lang.Double">
     *
     *
     *         <foreach collection="dateList" item="map" separator="UNION ALL">
     *             SELECT ifnull(SUM(amount),0 ) FROM orders
     *             <where>
     *                          <!-- 开始时间 -->
     *                         <if test="map.begin != null">
     *                             AND order_time >= #{map.begin}
     *                         </if>
     *                         <!-- 结束时间 -->
     *                         <if test="map.end != null">
     *                             AND #{map.end} >= order_time
     *                         </if>
     *                         <!-- 状态 -->
     *                         <if test="map.status != null">
     *                             AND status = #{map.status}
     *                         </if>
     *
     *             </where>
     *         </foreach>
     *
     *
     *         </select>
     *         //TODO 批量更新第一种方法，推荐使用
     *         <update id="batchUpdate">
     *     UPDATE table_name
     *     SET
     *         column1 = CASE id
     *             <foreach collection="list" item="item">
     *                 WHEN #{item.id} THEN #{item.value1}
     *             </foreach>
     *         END,
     *         column2 = CASE id
     *             <foreach collection="list" item="item">
     *                 WHEN #{item.id} THEN #{item.value2}
     *             </foreach>
     *         END
     *     WHERE id IN
     *     <foreach collection="list" item="item" open="(" separator="," close=")">
     *         #{item.id}
     *     </foreach>
     * </update>
     *
     * //TODO 批量更新第二种方法
     * <update id="batchUpdate">
     *     <foreach collection="list" item="item" separator=";">
     *         UPDATE table_name
     *         SET column1 = #{item.value1},
     *             column2 = #{item.value2}
     *         WHERE id = #{item.id}
     *     </foreach>
     * </update>
     */
}
