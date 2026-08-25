package com.gao.demo08.resume.model;

import java.util.List;

/**
 * 简历数据模型
 */
public record ResumeInfo(
        /*
          个人信息
         */
        PersonalInfo personal,
        /*
         * 工作经验
         */
        List<WorkExperience> workExperiences,
        /*
         * 学历信息
         */
        List<Education> education,
        /*
         * 技能
         */
        List<String> skills,
        /*
         * 证书
         */
        List<String> certifications,
        /*
         * 简历总结
         */
        String summary
) {
    /**
     * 个人信息
     */
    public record PersonalInfo(
            /*
             * 名字
             */
            String name,
            /*
             * 电邮
             */
            String email,
            /*
             * 电话号码
             */
            String phone,
            /*
             * 地址
             */
            String location,
            /*
             * 链接edin profile链接
             */
            String linkedinUrl
    ) {}

    /**
     * 工作经验
     */
    public record WorkExperience(
            /*
             * 企业名称
             */
            String company,
            /*
             * 职位
             */
            String title,
            /*
             * 开工日期
             */
            String startDate,
            /*
             * 结束日期（null 表示至今）
             */
            String endDate,
            /*
             * 奖金成就
             */
            List<String> achievements
    ) {}

    /**
     * 学历信息
     */
    public record Education(
            /*
             * 学校名称
             */
            String institution,
            /*
             * 学位
             */
            String degree,
            /*
             *.major
             */
            String major,
            /*
             * 毕业年份
             */
            String graduationYear
    ) {}
}
