package ucb.judge.ujusers.mappers

import ucb.judge.ujusers.dao.Campus
import ucb.judge.ujusers.dto.CampusDto

class CampusMapper {
    companion object {
        fun entityToDto(campus: Campus): CampusDto {
            return CampusDto(
                campusId = campus.campusId,
                name = campus.name
            )
        }
    }
}