package ucb.judge.ujusers.mappers

import ucb.judge.ujusers.dao.CampusMajor
import ucb.judge.ujusers.dto.CampusMajorDto

class CampusMajorMapper {
    companion object {
        fun entityToDto(campusMajor: CampusMajor): CampusMajorDto {
            return CampusMajorDto(
                campusMajorId = campusMajor.campusMajorId,
                campus = CampusMapper.entityToDto(campusMajor.campus!!),
                major = MajorMapper.entityToDto(campusMajor.major!!)
            )
        }
    }
}